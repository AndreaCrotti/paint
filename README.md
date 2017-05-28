# paint

[![Build Status](https://travis-ci.org/AndreaCrotti/paint.svg?branch=master)](https://travis-ci.org/AndreaCrotti/paint)

Create an abstract image and allow to operate over it.

## Usage

It's an interactive program that interprets line by line to see an example usage run:

    lein run < sample_input.txt

## Rationale

An image is just represented as a vector of vector of keywords.

The library core.matrix is used for some of of the image manipulations internall, but the type
of the matrixes passed around is still always a simple vector of vectors.

Only one function (`cli.clj:handle-line`) has side effects, because it's using an atom for storing
what's the current image we are working on.
This was not 100% necessary, but since this program has an implicit global state for the user it
made sense to use an atom to store that.

The most complex part was to get the algorithm for filling in a region.
This was implemented in `filling.clj:fill-coordinates` as a recursive function which returns
the whole list of coordinates, without actually doing any mutation to the image itself.

This algorithm (inspired from the flood-fill algorithm) moves in the 4 cardinal directions recursively
until every part of the image with the same colour has been discovered.

## License

Copyright © 2017 Andrea Crotti

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
