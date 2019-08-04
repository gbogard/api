let index = "/";
let singleCourse = id => "/courses" ++ id;

[@react.component]
let make = () => {
  let url = ReasonReactRouter.useUrl();

  switch (url.path) {
  | [] => <CoursesScene />
  | ["courses", id] => <SingleCourseScene id />
  | _ => React.string("Not found")
  };
};
