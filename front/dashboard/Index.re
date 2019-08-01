module Router = {
  [@react.component]
  let make = () => {
    let url = ReasonReactRouter.useUrl();

    switch (url.path) {
    | [] => <CoursesScene />
    | ["courses", id] => <SingleCourseScene id />
    | _ => React.string("Not found")
    };
  };
};

let app = <Store.Provider> <Router /> </Store.Provider>;

ReactDOMRe.renderToElementWithId(app, "app");
