type result('a) =
  | NotAsked
  | Loading
  | Failed
  | Success('a);
