[@react.component]
let make = () => {
  let url = ReasonReactRouter.useUrl();

  switch (url.path) {
  | [] => <CoursesScene />
  | ["courses", id] => <SingleCourseScene id pageId=None />
  | ["courses", id, pageId] => <SingleCourseScene id pageId=Some(pageId) />
  | _ => React.string("Not found")
  };
};
