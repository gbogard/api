open Types;
open Store.State;
open Courses;
open Rationale;

let renderWidgets = widgets =>
  widgets
  |> List.map(w => <WidgetComponent widget=w />)
  |> Array.of_list
  |> React.array;

module SimplePage = {
  [@react.component]
  let make = (~page: Page.simplePage, ~course, ~setCurrentPage) => {
    let firstPageId =
      course.pages |> RList.head |> Option.map(Utils.Page.extractId);
    let isFirstPage =
      firstPageId |> Option.map(id => id === page.id) |> Option.default(false);

    <div className="simple-page-layout">
      <Navbar />
      <PageNavigation
        pages={course.pages}
        currentPageId={page.id}
        onChange=setCurrentPage
      />
      <div className="content container-fluid">
        {
          if (isFirstPage) {
            <Box title="About this course">
              {React.string(course.description)}
            </Box>;
          } else {
            React.null;
          }
        }
        <h2> {React.string(page.title)} </h2>
        {page.widgets |> renderWidgets}
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

let renderCourse = (pageIdOpt, setCurrentPage, course) => {
  let pageOpt = pageIdOpt |> Option.flatMap(findPage(course));
  switch (pageOpt) {
  | Some(Page.SimplePage(p)) => <SimplePage page=p course setCurrentPage />
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

  let setCurrentPage = id =>
    dispatch(CoursesAction(SetCurrentPageId(Some(id))));

  Loader.renderResult(
    renderCourse(courses.currentPageId, setCurrentPage),
    courses.currentCourse,
  );
};
