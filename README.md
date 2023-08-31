# Overview: CNC Machine G-code Compiler

This application was developed as part of an independent study in Fall 2022 under Augustus Wendell.
The goal of the independent study was to augment a Genmitsu CNC 3018-PRO kit to realize AI-driven
and parametric artwork.
The repository represents one crucial subsection: designing and implementing an interface to
translate semantic drawing instructions into machine-readable G-code. This creates an abstraction
layer that benefits high-level art models, which can now operate independently of hardware
specifications.
At this point, the semantic instructions must be imported into the source code to facilitate
translation; however, the application is open to future extension where it will be able to receive
instructions over time or from an outside source while running.

---

# Capabilities

The application supports a variety of basic instructions, off-the-shelf composite instructions, and
sophisticated generative examples to demonstrate the wide-ranging possibilities of the fundamental
instruction set.

## Basic Instructions

* Move
* Set Workspace Limits
* Generate G-code Header/Footer

## Composite Instructions

* Draw a Line
* Draw a Rectangle/Circle/Regular Polygon
* Set/Return Home

## Generative Examples
* Draw Chaining Lines
* Draw Tangent/Overlapping Circles
* Draw Honeycomb Pattern
* Draw Function (Sine Wave)

---

Feel free to reach out to alec.liu AT duke.edu for questions!