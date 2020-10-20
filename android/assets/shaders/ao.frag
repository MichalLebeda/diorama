//https://badlogicgames.com/forum/viewtopic.php?t=29817&p=116723

#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;

uniform sampler2D depthText;
uniform sampler2D u_texture;
uniform vec2 camerarange;
uniform vec2 screensize;

float readDepth(in vec2 coord) {
    return (2.0 * camerarange.x) / (camerarange.y + camerarange.x - texture2D(depthText, coord).x * (camerarange.y - camerarange.x));
}

void main() {
    vec4 color = texture2D(depthText, v_texCoords);
    float depth = readDepth(v_texCoords);

    float d;
    float pw = 1.0 / screensize.x;
    float ph = 1.0 / screensize.y;
    float aoCap = 0.45;
    float ao = 0.0;
    float aoMultiplier=1000000.0;
    float depthTolerance = 0.00001;

    float step = 1.5f;

    d=readDepth(vec2(v_texCoords.x+pw, v_texCoords.y+ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x-pw, v_texCoords.y+ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x+pw, v_texCoords.y-ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x-pw, v_texCoords.y-ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    pw*=step;
    ph*=step;
    aoMultiplier/=step;

    d=readDepth(vec2(v_texCoords.x+pw, v_texCoords.y+ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x-pw, v_texCoords.y+ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x+pw, v_texCoords.y-ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x-pw, v_texCoords.y-ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    pw*=step;
    ph*=step;
    aoMultiplier/=step;

    d=readDepth(vec2(v_texCoords.x+pw, v_texCoords.y+ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x-pw, v_texCoords.y+ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x+pw, v_texCoords.y-ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x-pw, v_texCoords.y-ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    pw*=step;
    ph*=step;
    aoMultiplier/=step;

    d=readDepth(vec2(v_texCoords.x+pw, v_texCoords.y+ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x-pw, v_texCoords.y+ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x+pw, v_texCoords.y-ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x-pw, v_texCoords.y-ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    pw*=step;
    ph*=step;
    aoMultiplier/=step;

    d=readDepth(vec2(v_texCoords.x+pw, v_texCoords.y+ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x-pw, v_texCoords.y+ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x+pw, v_texCoords.y-ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x-pw, v_texCoords.y-ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    pw*=step;
    ph*=step;
    aoMultiplier/=step;

    d=readDepth(vec2(v_texCoords.x+pw, v_texCoords.y+ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x-pw, v_texCoords.y+ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x+pw, v_texCoords.y-ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    d=readDepth(vec2(v_texCoords.x-pw, v_texCoords.y-ph));
    ao+=min(aoCap, max(0.0, depth-d-depthTolerance) * aoMultiplier);

    ao/=32.0;

    gl_FragColor = vec4(1.2-ao, 1.2-ao, 1.2-ao, 1) * texture2D(u_texture, v_texCoords);
}