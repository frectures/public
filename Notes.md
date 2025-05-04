## Bugs

1. Pieces can be rotated into the border
   - Rotation + horizontal movement destroys the border
2. When the current piece is close to the ground, penalty lines “grow into them”
   - What should happen instead? Not trivial!
3. When removing `n` lines:
   - All lines are copied down correctly, but
   - The top `n` lines remain where they are

## Missing features

- Scoring system for hard drops and removed lines
- Removing lines should accelerate the game
- Both players should get the same sequence of pieces
- Show which piece comes next
- Store bad piece for later use

## Code review

- ⚠️ Do not change anything yet! ⚠️

### Fields

- Why is the line `private static final byte[] PERMUTATIONS = ...` 80746 characters long?!
- `PERMUTATIONS` and `permutationIndex` belong in their own class `FairLetterSupplier`
  - Fred will (have) demonstrate(d) this live
- `int[] board` should probably be `SomeEnumType[] board`
- `letter` and `rotation` and `shape` should probably be replaced with some `Shape` type
  - This is far from trivial!
- Should a `Tetris` object know its `opponent` at all?
  - What about single player games?
- Public fields like `gameOver` are generally frowned upon

### `Tetris()`

- Why initialize `permutationIndex` with `7`?
- Why initialize `position` with `6`?
- The constructor does not initialize all fields explicitly
  - Is this good or bad? Depends on your Team's code conventions
  - `opponent` *cannot* be initialized in the constructor. Why?

### `storePiece()`

- The 4 assignments *could* be replaced with a loop
- But I find the 4 assignments easier to understand

### `moveLeft()` and `moveRight()`

- The `if` condition is exactly the same in both methods

### `moveDown()`

- Same `if` condition, again
  - The follwing `if` condition is even more distracting
- What does `n` stand for? Poor variable name
  - What is the purpose of the `switch (n)` statement?
  - Hint: If *every* removed line were sent to the opponent, the game would end too quickly
- The 3 `arraycopy` calls are full of magic numbers
  - Why is there only 1 `arraycopy` call for removing lines, but 2 `arraycopy` calls for inserting lines?
  - Hint: This is related to bug #3
- When does `if (PERMUTATIONS[permutationIndex] == 0)` trigger?
  - What is the significance of `8` and `5040`?
- Same `gameOver = ...` condition, again
- Overall, `moveDown()` is *far* too complex!

### `EMPTY_BOARD`

- On the one hand, it's nice to see the empty board “spelled out” like that
- On the other hand, it would be nice if the initialization required less space

### `SHAPES`

- What do the hex literals (like `0x23`) mean?
  - row `2`
  - column `3`
- What does each array dimension mean?
  - What is `SHAPES[1][0][3]`?
  - What is `SHAPES[1][0]`?
  - What is `SHAPES[1]`?
  - What is `SHAPES`?

### `PENALTY_LINES`

- Far too many (repeated) numbers
- We should probably remove `PENALTY_LINES` altogether

## Terminology

- What is the difference between the following terms?
  - Letter
  - Shape
  - Piece
- Is `Tetris` even a good name for the class?
  - We probably won't change it anymore
  - But it's a question worth asking!
