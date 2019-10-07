[@react.component]
let make = () => {
  let url = ReasonReactRouter.useUrl();

  React.useEffect1(
    () => {
      Interop.Dom.scrollTo(Interop.Dom.window, 0, 0);
      None;
    },
    [|url|],
  );

  switch (url.path) {
  | [] => <CoursesScene />
  | ["courses", id] => <SingleCourseScene id pageId=None />
  | ["courses", id, pageId] => <SingleCourseScene id pageId={Some(pageId)} />
  | _ => React.string("Not found")
  };
};