open Types;
open Store.State;
open Courses;
open Rationale;

let renderWidgets = (widgets, onCheck, widgetsState, resetWidget) =>
  widgets
  |> List.map(w => {
       let widgetId = Utils.Widget.extractId(w);
       let state =
         Courses.State.Map.getWithDefault(widgetsState, widgetId, Initial);
       <WidgetComponent
         key={Utils.Widget.extractId(w)}
         widget=w
         state
         onCheck={input => onCheck(widgetId, input)}
         resetWidget={() => resetWidget(widgetId)}
       />;
     })
  |> Array.of_list
  |> React.array;

module SimplePage = {
  [@react.component]
  let make =
      (
        ~page: Page.simplePage,
        ~course,
        ~setCurrentPage,
        ~widgetsState,
        ~checkWidget,
        ~resetWidget,
      ) => {
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
        {renderWidgets(page.widgets, checkWidget, widgetsState, resetWidget)}
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

let renderCourse =
    (
      pageIdOpt,
      setCurrentPage,
      widgetsState,
      checkWidget,
      resetWidget,
      course,
    ) => {
  let pageOpt = pageIdOpt |> Option.flatMap(findPage(course));
  switch (pageOpt) {
  | Some(Page.SimplePage(p)) =>
    <SimplePage
      page=p
      course
      setCurrentPage
      widgetsState
      checkWidget
      resetWidget
    />
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

  let checkWidgeet = (id, input) =>
    dispatch(CoursesAction(CheckWidget(id, input)));

  let resetWidget = id =>
    dispatch(CoursesAction(SetWidgetState(id, Initial)));

  Loader.renderResult(
    renderCourse(
      courses.currentPageId,
      setCurrentPage,
      courses.widgetsState,
      checkWidgeet,
      resetWidget,
    ),
    courses.currentCourse,
  );
};
