# AP Physics 1 Simulation & Validation Engine

A Java-based physics simulator built to model, visualize, and **validate** every major topic in AP Physics 1 — kinematics, dynamics, circular motion and gravitation, energy, momentum, simple harmonic motion, and torque/rotation — against their closed-form theoretical predictions.

The core goal isn't just to animate physics, but to check that the simulation actually agrees with the equations it's supposed to represent, and to understand *where and why* that agreement breaks down when a model's assumptions (like the small-angle pendulum approximation) are pushed past their limits.

---

## What this is

This started as a personal project to deepen my understanding of AP Physics 1 while taking the course, and grew into a full simulation engine with:

- **11 interactive scenarios** covering all 7 AP1 units, adjustable via sliders (no code editing required to run them)
- **A step-by-step solver** for common problem types, showing intermediate work rather than just a final answer
- **An AI-assisted problem solver** that accepts a plain-English word problem and returns a worked solution, with a matching simulation when one is available
- **A validation report** that runs each scenario's underlying physics against its known theoretical formula, measuring % error and reporting pass/fail — with inputs adjustable by the user, so results can be reproduced and pushed to failure on purpose

## Screenshots

*(add screenshots or a short screen recording here — the 3D view, a graph, and especially the validation report table showing a PASS turning into a FAIL as an assumption is pushed past its limit)*

## Units covered

| Unit | Topics | Scenarios |
|---|---|---|
| 1. Kinematics | SUVAT, free fall, angled/height-difference projectile motion, relative velocity | Angled Projectile |
| 2. Dynamics | Newton's laws, applied forces, static/kinetic friction, normal force (flat & inclined), tension/pulleys, equilibrium | Ramp with Friction, Atwood Machine |
| 3. Circular Motion & Gravitation | Centripetal force (string & friction), real inverse-square gravity, orbits | Vertical Loop, Car on a Flat Curve, Orbit |
| 4. Energy | KE/PE tracking with live conservation graphs, work-energy theorem, power | *(visible across all scenarios via the Energy tab)* |
| 5. Momentum | Impulse-momentum theorem, system-wide conservation, elastic/inelastic classification | Two-Ball Collision |
| 6. Simple Harmonic Motion | Springs (with energy tracking), pendulums (with measured period), SHM solver formulas | Mass on a Spring, Pendulum |
| 7. Torque & Rotation | Rotational kinematics, torque & moment of inertia, rolling without slipping, torque equilibrium | Rolling Race (Sphere vs. Hoop), Balance Beam |

## Model Validation

Every non-trivial physics module in this project is checked against a matching analytical formula, not just visually inspected. The **Validation Report** tab lets you pick a test, set the inputs yourself with sliders, run the simulation, and see the measured result compared against the theoretical prediction with a computed percent error.

Some representative results:

- **Atwood machine acceleration** — measured acceleration typically matches `a = g|m₁-m₂|/(m₁+m₂)` within ~1%.
- **Pendulum period** — at small release angles (under ~15°), measured period matches `T = 2π√(L/g)` within ~1%. **Deliberately pushing the release angle to 60–80° causes the test to fail** — this isn't a bug. It's the small-angle approximation breaking down exactly as expected, since large-angle pendulum motion is no longer simple harmonic motion. The test is built to demonstrate this limitation, not hide it.
- **Rolling race (sphere vs. hoop)** — the measured acceleration ratio between a solid sphere and a hoop of equal mass/radius matches the theoretical `(1 + I_hoop/mr²) / (1 + I_sphere/mr²)` ratio regardless of what mass, radius, or ramp angle you choose — a direct demonstration that mass cancels out of rolling dynamics while moment of inertia doesn't.
- **Projectile range** — matches `range = vₓ · t_flight` closely at a fine timestep. **Increasing the simulation's timestep (`dt`) on purpose measurably increases the error**, which is used here to directly illustrate numerical integration (discretization) error, separate from any error in the underlying physics model.

## Known limitations

This project models AP1-level mechanics under AP1's own standard simplifying assumptions, not full real-world physics:

- No air resistance or drag anywhere in the simulation
- Ropes and pulleys are treated as massless, frictionless, and inextensible
- Objects are rigid; rotation (Unit 7) is limited to a single fixed axis, matching how AP1 itself treats rotational motion
- Collision detection is discrete (checked once per timestep), so very fast objects could theoretically tunnel through thin surfaces in one frame
- The orbital gravitation demo uses an intentionally exaggerated gravitational constant for visualization — it is explicitly **not** to real astronomical scale

None of these are oversights — they match the same idealizations AP1 problems themselves are built on, which is what makes the simulation's results comparable to the course's own formulas in the first place.

## Running it

**Requirements:** Java 17+ (uses `java.net.http.HttpClient`, built in since Java 11)

```bash
javac -d out $(find src -name "*.java")
java -cp out main.Main
```

This opens the scenario launcher. From there you can run any of the 11 scenarios, open the step-by-step solver, try the AI problem solver, or open the validation report.

### Optional: AI Problem Solver setup

The AI Problem Solver tab uses Google's free-tier Gemini API to accept plain-English word problems.

1. Get a free API key at [aistudio.google.com/apikey](https://aistudio.google.com/apikey) (no credit card required)
2. Set it as an environment variable named `GEMINI_API_KEY` before running the app
3. This feature is entirely optional — every other part of the app works without it

## Project structure

```
math/       Vector math
scene/      GameObject (the core simulated body) and motion history tracking
physics/    PhysicsWorld and all force/constraint types (friction, tension, springs, circular motion, rolling, torque)
solver/     Closed-form analytical solvers for every unit, used both standalone and for validation
render/     3D rendering, graphs, and UI panels
launcher/   The scenario picker, step-by-step solver UI, AI solver UI, and validation report UI
validation/ The validation test suite comparing simulation output against theoretical predictions
ai/         Minimal JSON parsing and the Gemini API client for the AI solver feature
```

## Why this project

I wanted to test my own understanding of AP Physics 1 by building something that had to get the underlying equations exactly right to behave correctly — a bug wasn't just a crash, it was visibly wrong physics. The validation report grew out of wanting to hold the simulation itself accountable to the same standard: not just "does it look right," but "does it match the formula, and do I understand exactly where and why that stops being true."
