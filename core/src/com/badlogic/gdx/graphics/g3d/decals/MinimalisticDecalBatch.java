package com.badlogic.gdx.graphics.g3d.decals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by michal on 29.12.17.
 */

//NO SORTING AND SHIT

public class MinimalisticDecalBatch implements Disposable {
    private static final int DEFAULT_SIZE = 1000;
    public ShaderProgram shaderProgram;
    private float[] vertices;
    private Mesh mesh;
    private Array<Decal> decals;

    public MinimalisticDecalBatch(ShaderProgram shaderProgram) {
        initialize(DEFAULT_SIZE);
        decals = new Array<>();
        this.shaderProgram = shaderProgram;
    }

    public MinimalisticDecalBatch() {
        initialize(DEFAULT_SIZE);
        decals = new Array<>();
        compileShader();
    }

    public void add(Decal decal) {
        decals.add(decal);
    }

    private void beforeGroups() {
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_BLEND);
    }

    private void afterGroups() {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        clear();
    }

    private void compileShader() {
        String vertexShader = Gdx.files.internal("shaders/decal.vert").readString();
        String fragmentShader = Gdx.files.internal("shaders/decal.frag").readString();

        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        if (!shaderProgram.isCompiled())
            throw new IllegalArgumentException("couldn't compileShader defaultShader: " + shaderProgram.getLog());
    }

    private void clear() {
        decals.clear();
    }

    /**
     * @return maximum amount of decal objects this buffer can hold in memory
     */
    public int getSize() {
        return vertices.length / Decal.SIZE;
    }

    @Override
    public void dispose() {
        shaderProgram.dispose();

        clear();
        vertices = null;
        mesh.dispose();
    }

    public void render(Camera camera, Color backgroundColor,float time) {
        beforeGroups();

        ShaderProgram shaderProgram = this.shaderProgram;

        shaderProgram.bind();
        shaderProgram.setUniformMatrix("u_projectionViewMatrix", camera.combined);
        shaderProgram.setUniformi("u_texture", 0);
        shaderProgram.setUniformf("u_camera_pos", camera.position);
        shaderProgram.setUniformf("u_background_color", backgroundColor);
        shaderProgram.setUniformf("time", time);
        // batch vertices
        DecalMaterial lastMaterial = null;
        int idx = 0;
        for (Decal decal : decals) {
            if (lastMaterial == null || !lastMaterial.equals(decal.getMaterial())) {
                if (idx > 0) {
                    flush(shaderProgram, idx);
                    idx = 0;
                }
                decal.material.set();
                lastMaterial = decal.material;
            }

//            decal.lookAt(camera.position, Vector3.Z);
            decal.update();
            System.arraycopy(decal.vertices, 0, vertices, idx, decal.vertices.length);
            idx += decal.vertices.length;
            // if our batch is full we have to flush it
            if (idx == vertices.length) {
                flush(shaderProgram, idx);
                idx = 0;
            }
        }
        // at the end if there is stuff left in the batch we renderGroups that
        if (idx > 0) {
            flush(shaderProgram, idx);
        }

        afterGroups();
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
        vertices = new float[size * Decal.SIZE];

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
