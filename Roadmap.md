## Introduce constants

- Below are obvious candidates for introducing `static final` constants:
- `permutationIndex = 7`
- `position = 6`
  - `position = 6` again, later
  - `position != 6`, later
- `5040`
- `for (int i = 0; i < 320; i += 16)`
  - There already is a constant for `16`
  - `320` should be replaced with a formula...
  - ...maybe involving a new constant
- Magic numbers in `System.arraycopy` calls
- There's probably more

## Rename/split variable

- Fortunately, there is only 1 really badly named variable:
  - `n`
  - What does `n` stand for?
  - number of lines
- `lines` would already be a better name than `n`, but:
  - above `switch (n)`, it means *removed* lines
  - below `switch (n)`, it means *penalty* lines
- Replace the 1 variable `n` with 2 variables:
  - `removedLines`
  - `penaltyLines`
- Generally speaking, having the meaning of 1 variable change during execution confuses maintainers!

## Extract methods

- Below are the methods I would extract
  - `(...)` means *you* think about the parameter list!
- `void pickNextPiece()`
  - modifies `letter` and `rotation` and `shape` 
  - should *also* be called during construction
  - Otherwise, the very first piece is always the same
- `boolean pieceCollides()`
  - or `boolean pieceWouldCollide(int positionDelta)`
  - Call this method inside `rotate` to fix bug #1
- `void storePiece(int value)` already exists
  - Replace the 4 assigments inside `moveDown()` with a call to `storePiece`
- `boolean lineIsFull(...)` and `void removeFullLine(...)`
  - Alternatively, just one method `int removeFullLines()`
  - Can you fix bug #3 here?
- `void moveLinesUp(...)` and `void insertPenaltyLines(...)`
  - Alternatively, fuse both methods into 1
- Maybe extract the `switch` statement into its own method?
- After these extractions, `moveDown()` should be *far* more readable!

## Add comments

- `NOTES.md` was full of questions
- Some have already vanished thanks to constants, renamings and extractions
- Answer/clarify remaining questions with comments!

## Extract classes

### Shape 🧑‍🦯

- Create `enum Shape`
  - with 7*4 = 28 instances like `I0`, `I90`, `I180`, `I270`, `J0`, `J90`, `J180`, `J270`...
  - or 19 distinct instances like `I0`, `I90`, `J0`, `J90`, `J180`, `J270`...
  - add 4 private `int` variables
  - add constructor for those
  - initialize 28 (or 19) instances
  - add static `randomShape()` method (`switch` helps)
  - add `rotated()` method (`switch` helps, again)
- Inside `class Tetris`
  - remove `static final int[][][] SHAPES`
  - replace `int letter, rotation, shape[]` with `Shape shape`
  - call `randomShape` and `rotated` at appropriate places
- But what about `storePiece` and `pieceCollides`?
  - Either provide getters for the 4 `int` variables (anemic design)
  - Or pass `board` and `position` into `Shape` methods (cyclic dependency)
  - Or treat `Shape` like a collection/stream:

```java
public void storePiece(int value) {
    shape.f(positionDelta -> board[position + positionDelta] = value);
}

public boolean pieceCollides() {
    return shape.g(positionDelta -> board[position + positionDelta] != 0);
}
```

- Of course `f` and `g` are terrible names!
  - What do `f` and `g` actually do here?
  - Can you find better names?
  - Prefer standard names from the `java.util.stream.Stream` interface
  - Many Java programmers are already familiar with those

### Line ☁️🏰

- Extracting a `class Line` would probably simplify line removal/insertion
- Maybe changing `int[] board` to `int[][] board` would already suffice?
- In either case, `Shape` would have to accomodate for the added dimension

### Board ☁️🏰

- Extracting a `class Board` would enable us to test the 3 bugs before fixing them

## Tetris opponent; ☁️🏰

- Every `Tetris` object knows its `opponent`
  - This implies duels, always
  - What if we want to play friendly/independently?
  - Easy fix: allow `opponent = null`, check for `opponent == null`
- Alternatively, replace `Tetris opponent` with...
  - `IntConsumer onLinesRemoved` (nullable)
  - `IntConsumer onLinesRemoved` (non-nullable, with Null-Object-Pattern for single player games)
  - `IntConsumer[] onLinesRemoved` (non-nullable, empty for single player games)
