type result('a) =
  | NotAsked
  | Loading
  | Failure
  | Success('a);
