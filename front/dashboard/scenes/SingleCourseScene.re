open Types;
open Store.State;
open Courses;
open Rationale;

module SimplePage = {
  [@react.component]
  let make = (~page: Page.simplePage, ~course) => {
    let firstPageId =
      course.pages |> RList.head |> Option.map(Utils.Page.extractId);
    let isFirstPage =
      firstPageId |> Option.map(id => id === page.id) |> Option.default(false);

    <div className="simple-page-layout">
      <Navbar />
      <div className="sidebar" />
      <div className="content">
        {
          if (isFirstPage) {
            <p> {React.string(course.description)} </p>;
          } else {
            React.null;
          }
        }
        <h2> {React.string(page.title)} </h2>
      </div>
    </div>;
  };
};

module CodePage = {
  [@react.component]
  let make = (~page: Page.codePage) =>
    <div> <h2> {React.string(page.title)} </h2> </div>;
};

let findPage = (course, id) =>
  RList.find(p => Utils.Page.extractId(p) == id, course.pages);

let renderCourse = (pageIdOpt, course) => {
  let pageOpt = pageIdOpt |> Option.flatMap(findPage(course));
  switch (pageOpt) {
  | Some(Page.SimplePage(p)) => <SimplePage page=p course />
  | Some(Page.CodePage(p)) => <CodePage page=p />
  | _ => React.null
  };
};

[@react.component]
let make = (~id: string) => {
  let ({courses}, dispatch) = Store.useStore();

  React.useEffect0(() => {
    dispatch(CoursesAction(FetchCourse(id)));
    Some(() => dispatch(CoursesAction(SetCurrentCourse(NotAsked))));
  });

  Loader.renderResult(
    renderCourse(courses.currentPageId),
    courses.currentCourse,
  );
};
