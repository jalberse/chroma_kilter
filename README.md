# chroma_kilter

| Keys  | Action |
|---|---|
| p  |  Pause rotation |
| i, o  |  Change rotation speed |
| k, l |  Change distance to object |
| 0-7 | Change scene |
| z,x,c | Toggle render modes (nondestructive, basic, nodepth) |
| w | Toggle wireframe |
| up, down | change strength of chromatic aberration (increase, decrease) |
| left, right | rotate aberration axis (counterclockwise, clockwise) |
| period, slash | change distance of focal plane from screen |
| minus, equals | toggle background color |

## TODO

- Enable user to increase/decrease the alpha of the chromatic aberrations (a different way to control its "strength")
- **Distance calculations do not necessarily work with how scenes are constructed now**
  - Not enough to sum distance of scene and local geometry - additional transforms are there in the scene, should be able to account for this
- Enable user to toggle between colors for models
- JAMES: Functions for distance/chromab strength
  - Let user toggle
