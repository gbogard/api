open Types;

let renderResult = fn =>
  fun
  | NotAsked => React.null
  | Loading => React.string("Loading ...")
  | Failed
  | ClientError(_) => React.string("Failed")
  | Success(data) => fn(data);
