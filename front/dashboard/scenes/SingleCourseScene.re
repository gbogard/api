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

let renderNavigationButtons = (course, currentPageId, setCurrentPage) => {
  let currentPageIndex =
    course.pages
    |> RList.findIndex(p => Utils.Page.extractId(p) === currentPageId);
  let previousPage =
    currentPageIndex
    |> Option.map(i => i - 1)
    |> Option.flatMap(RList.nth(_, course.pages));
  let nextPage =
    currentPageIndex
    |> Option.map(i => i + 1)
    |> Option.flatMap(RList.nth(_, course.pages));
  let navigate = (page, _) => setCurrentPage(Utils.Page.extractId(page));

  <div className="spaced-container with-padding-y">
    <div>
      {
        previousPage
        |> Option.map(page =>
             <Button onClick={navigate(page)}>
               <i className="ion ion-ios-arrow-back" />
               {React.string(Utils.Page.extractTitle(page))}
             </Button>
           )
        |> Option.default(React.null)
      }
    </div>
    <div>
      {
        nextPage
        |> Option.map(page =>
             <Button onClick={navigate(page)}>
               {React.string(Utils.Page.extractTitle(page))}
               <i className="ion ion-ios-arrow-forward" />
             </Button>
           )
        |> Option.default(React.null)
      }
    </div>
  </div>;
};

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
    let (isDrawerOpen, setDrawerOpen) = React.useState(() => false);

    let firstPageId =
      course.pages |> RList.head |> Option.map(Utils.Page.extractId);
    let isFirstPage =
      firstPageId |> Option.map(id => id === page.id) |> Option.default(false);
    let navigation =
      <PageNavigation
        pages={course.pages}
        currentPageId={page.id}
        onChange=setCurrentPage
      />;

    <>
      <Interop.Drawer
        _open=isDrawerOpen onChange={state => setDrawerOpen(_ => state)}>
        navigation
      </Interop.Drawer>
      <div className="simple-page-layout">
        <Navbar onDrawerOpen={() => setDrawerOpen(_ => true)} />
        navigation
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
          {
            renderWidgets(page.widgets, checkWidget, widgetsState, resetWidget)
          }
          {renderNavigationButtons(course, page.id, setCurrentPage)}
        </div>
      </div>
    </>;
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
