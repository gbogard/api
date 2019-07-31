open Types;

let renderResult = (fn, result: result('a)) =>
  switch (result) {
  | NotAsked => React.null
  | Loading => React.string("Loading ...")
  | Failed => React.string("Failed")
  | Success(data) => fn(data)
  };
