package com.badlogic.gdx.graphics.g3d.decals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

import cz.orache.honza.entity.Hero;
import cz.orache.honza.graphics.DecalGroup;
import cz.orache.honza.graphics.ParamBasedShaderProgram;

/**
 * Created by michal on 29.12.17.
 */

//NO SORTING AND SHIT

public class MinimalisticDecalBatch implements Disposable {
    public static final int GROUP_TYPE_COMMON = 0;
    public static final int GROUP_TYPE_COMMON_REFLECTED = 1;
    public static final int GROUP_TYPE_VEGETATION = 2;
    public static final int GROUP_TYPE_VEGETATION_REFLECTED = 3;
    public static final int GROUP_TYPE_VANISHING_WALL = 4;
    private static final int DEFAULT_SIZE = 1000;
    public ParamBasedShaderProgram defaultShader;
    public ParamBasedShaderProgram waterReflectionShader;
    public ParamBasedShaderProgram vegetationShader;
    public ParamBasedShaderProgram waterReflectionVegetationShader;
    public ParamBasedShaderProgram vanishingWallsShader;
    private float time = 0; //for shader
    private float[] vertices;
    private Mesh mesh;
    private DecalGroup[] decalGroupsToFlush;
    private Camera camera;
    private Hero hero;

    public MinimalisticDecalBatch(Camera camera, Hero hero) {
        this.camera = camera;
        this.hero = hero;

        initialize(DEFAULT_SIZE);
        compileShader();
        decalGroupsToFlush = new DecalGroup[5];
        decalGroupsToFlush[GROUP_TYPE_COMMON] = new DecalGroup(defaultShader);
        decalGroupsToFlush[GROUP_TYPE_COMMON_REFLECTED] = new DecalGroup(waterReflectionShader);
        decalGroupsToFlush[GROUP_TYPE_VEGETATION] = new DecalGroup(vegetationShader);
        decalGroupsToFlush[GROUP_TYPE_VEGETATION_REFLECTED] = new DecalGroup(waterReflectionVegetationShader);
        decalGroupsToFlush[GROUP_TYPE_VANISHING_WALL] = new DecalGroup(vanishingWallsShader);
    }

    public void add(BaseDecal entity, int GROUP_TYPE) {
       /* if(GROUP_TYPE == GROUP_TYPE_VEGETATION_REFLECTED||GROUP_TYPE==GROUP_TYPE_COMMON_REFLECTED){
            return;
        }*/
        decalGroupsToFlush[GROUP_TYPE].add(entity);
    }

    public void updateTime(float delta) {
        time += delta;
    }

    public void renderGroups(int... groupIds) {
        beforeGroups();

        for (int groupId : groupIds) {
            DecalGroup group = decalGroupsToFlush[groupId];
            beforeEverySingleGroup(group);
            renderGroup(group);
            afterEverySingleGroup(group);
        }

        afterGroups();
        clear(groupIds);
    }

    private void beforeEverySingleGroup(DecalGroup group) {
        ParamBasedShaderProgram shaderProgram = group.getShaderProgram();

        shaderProgram.begin();
        shaderProgram.setUniformMatrix("u_projectionViewMatrix", camera.combined);
        shaderProgram.setUniformi("u_texture", 0);

        if (shaderProgram.acceptsTime()) {
            shaderProgram.setUniformf("u_time", time % (MathUtils.PI2));
        }

        if (shaderProgram.acceptsPlayerPos()) {
            shaderProgram.setUniformf("u_player_pos", hero.getPosition().x, hero.getPosition().y, 1.4f, 0);
        }

    }

    private void afterEverySingleGroup(DecalGroup group) {
        group.getShaderProgram().end();
    }

    private void beforeGroups() {
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_BLEND);
    }

    private void afterGroups() {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void compileShader() {
        String vertexShader = Gdx.files.internal("shaders/v.glsl").readString();
        String fragmentShader = Gdx.files.internal("shaders/f.glsl").readString();

        String flippedVertexShader = Gdx.files.internal("shaders/vFlipped.glsl").readString();
        String wobblyFragmentShader = Gdx.files.internal("shaders/fWobbly.glsl").readString();

        String vanishingWallsVertexShader = Gdx.files.internal("shaders/vVanishingWall.glsl").readString();
        String vanishingWallsFragmentShader = Gdx.files.internal("shaders/fVanishingWall.glsl").readString();

        String vegetationVertex = Gdx.files.internal("shaders/vVegetation.glsl").readString();

        String vegetationFlippedVertex = Gdx.files.internal("shaders/vVegetationFlipped.glsl").readString();

        defaultShader = new ParamBasedShaderProgram(vertexShader, fragmentShader, false, false);
        if (defaultShader.isCompiled() == false)
            throw new IllegalArgumentException("couldn't compileShader defaultShader: " + defaultShader.getLog());

        waterReflectionShader = new ParamBasedShaderProgram(flippedVertexShader, wobblyFragmentShader, true, false);
        if (waterReflectionShader.isCompiled() == false)
            throw new IllegalArgumentException("couldn't compileShader waterReflectionShader: " + waterReflectionShader.getLog());

        vegetationShader = new ParamBasedShaderProgram(vegetationVertex, fragmentShader, true, false);
        if (defaultShader.isCompiled() == false)
            throw new IllegalArgumentException("couldn't compileShader vegetationShader: " + vegetationShader.getLog());

        waterReflectionVegetationShader = new ParamBasedShaderProgram(vegetationFlippedVertex, wobblyFragmentShader, true, false);
        if (waterReflectionVegetationShader.isCompiled() == false)
            throw new IllegalArgumentException("couldn't compileShader waterReflectionVegetationShader: " + waterReflectionVegetationShader.getLog());

        vanishingWallsShader = new ParamBasedShaderProgram(vanishingWallsVertexShader, vanishingWallsFragmentShader, false, true);
        if (waterReflectionVegetationShader.isCompiled() == false)
            throw new IllegalArgumentException("couldn't compileShader waterReflectionVegetationShader: " + vanishingWallsShader.getLog());

    }

    private void clear(int... groupIds) {
        for (int groupId : groupIds) {
            decalGroupsToFlush[groupId].clear();
        }
    }

    private void clearAll() {
        for (DecalGroup group : decalGroupsToFlush) {
            group.clear();
        }
    }

    /**
     * @return maximum amount of decal objects this buffer can hold in memory
     */
    public int getSize() {
        return vertices.length / BaseDecal.SIZE;
    }

    @Override
    public void dispose() {
        defaultShader.dispose();
        waterReflectionShader.dispose();
        vegetationShader.dispose();
        waterReflectionVegetationShader.dispose();

        clearAll();
        vertices = null;
        mesh.dispose();
    }

    private void renderGroup(DecalGroup decals) {
        ShaderProgram shader = decals.getShaderProgram();
        // batch vertices
        DecalMaterial lastMaterial = null;
        int idx = 0;
        for (BaseDecal decal : decals) {
            if (lastMaterial == null || !lastMaterial.equals(decal.getMaterial())) {
                if (idx > 0) {
                    flush(shader, idx);
                    idx = 0;
                }
                decal.material.set();
                lastMaterial = decal.material;
            }

            decal.update();
            System.arraycopy(decal.vertices, 0, vertices, idx, decal.vertices.length);
            idx += decal.vertices.length;
            // if our batch is full we have to flush it
            if (idx == vertices.length) {
                flush(shader, idx);
                idx = 0;
            }
        }
        // at the end if there is stuff left in the batch we renderGroups that
        if (idx > 0) {
            flush(shader, idx);
        }
    }

    /**
     * Flushes vertices[0,verticesPosition[ to GL verticesPosition % BaseDecal.SIZE must equal 0
     *
     * @param verticesPosition Amount of elements from the vertices array to flush
     */
    protected void flush(ShaderProgram shader, int verticesPosition) {
        mesh.setVertices(vertices, 0, verticesPosition);
        mesh.render(shader, GL20.GL_TRIANGLES, 0, verticesPosition / 4);
    }

    public void initialize(int size) {
        vertices = new float[size * BaseDecal.SIZE];

        Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexArray;
        if (Gdx.gl30 != null) {
            vertexDataType = Mesh.VertexDataType.VertexBufferObjectWithVAO;
        }
        mesh = new Mesh(vertexDataType, false, size * 4, size * 6, new VertexAttribute(
                VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(
                VertexAttributes.Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE), new VertexAttribute(
                VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

        short[] indices = new short[size * 6];
        int v = 0;
        for (int i = 0; i < indices.length; i += 6, v += 4) {
            indices[i] = (short) (v);
            indices[i + 1] = (short) (v + 2);
            indices[i + 2] = (short) (v + 1);
            indices[i + 3] = (short) (v + 1);
            indices[i + 4] = (short) (v + 2);
            indices[i + 5] = (short) (v + 3);
        }
        mesh.setIndices(indices);
    }
}
