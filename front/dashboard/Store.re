open Utils;
open Types;

type action =
  | FetchCourses
  | SetCourses(result(list(courseManifest)));

module State = {
  type t = {courses: result(list(courseManifest))};

  let initial = {courses: NotAsked};

  let reducer = (state, action) =>
    switch (action) {
    | FetchCourses => state
    | SetCourses(courses) => {courses: courses}
    };
};

module Effects = {};

module Provider = {
  let ctx = React.createContext((State.initial, _action => ()));
  let provider = React.Context.provider(ctx);

  [@react.component]
  let make = (~children) => {
    let (state, dispatch) = React.useReducer(State.reducer, State.initial);
    React.createElement(
      provider,
      {"value": (state, dispatch), "children": children},
    );
  };
};

let useStore = () => React.useContext(Provider.ctx);