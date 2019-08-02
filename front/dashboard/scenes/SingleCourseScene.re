open Types;
open Store.State;
open Courses;
open Rationale;

module SimplePage = {
  [@react.component]
  let make = (~page: Page.simplePage) =>
    <div className="container-fluid px-0 h-100">
      <div className="row">
        <div className="col-md-2 col-lg-1">
          {React.string("Foo bar baz")}
        </div>
        <div className="col"> <h2> {React.string(page.title)} </h2> </div>
      </div>
    </div>;
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
  | Some(Page.SimplePage(p)) => <SimplePage page=p />
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

  <div>
    <Navbar />
    {
      Loader.renderResult(
        renderCourse(courses.currentPageId),
        courses.currentCourse,
      )
    }
  </div>;
};
