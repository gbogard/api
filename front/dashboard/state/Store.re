type action =
  | CoursesAction(Courses.action)
  | UsersAction(Users.action);

module State = {
  type t = {
    courses: Courses.State.t,
    users: Users.State.t,
  };

  type selector('a) = t => 'a;

  let initial = {courses: Courses.State.initial, users: Users.State.initial};
};

module Provider = {
  let ctx = React.createContext((State.initial, _action => ()));
  let provider = React.Context.provider(ctx);

  [@react.component]
  let make = (~children) => {
    let (usersState, usersDispatch) =
      ReactUpdate.useReducer(Users.State.initial, Users.State.reducer);
    let (coursesState, coursesDispatch) =
      ReactUpdate.useReducer(Courses.State.initial, Courses.State.reducer);

    let combinedState: State.t = {courses: coursesState, users: usersState};

    let combinedDispatch = action =>
      switch (action) {
      | UsersAction(action) => usersDispatch(action)
      | CoursesAction(action) => coursesDispatch(action)
      };

    React.createElement(
      provider,
      {"value": (combinedState, combinedDispatch), "children": children},
    );
  };
};

let useStore = () => React.useContext(Provider.ctx);
let useSelector = (selector: State.selector('a)) => {
  let (state, _) = useStore();
  selector(state);
};
