# chroma_kilter

| Keys  | Action |
|---|---|
| p  |  Pause rotation |
| i, o  |  Change rotation speed |
| k, l |  Change distance to object |
| 0-9 | Change geometry |
| up | increase strength of chromatic abberation |
| down | decrease strength of chromatic abberation | 
| left, right | toggle background color |

## TODO

- Enable user to toggle between colors for models
- More geometries
	- Sphere, dodeca, cone, teapot, monkey head ...
- Further experimentation with effect
  - Projection to back plane for abberation
  - Different blending modes - how to get non-destructive color on original model?
- Multiple objects at once to observe interaction
- Tie effect to distance
- Consider generalizing code for effect (simply pass geometry for GL_QUADS, TRIS etc instead of manually for each drawn object)
- Very minor stress testing
