open Types;
open Store.State;
open Courses;
open Rationale;

let findPage = (course, id) =>
  RList.find(p => Utils.Page.extractId(p) == id, course.pages);

let coursePageUrl = (course, pageId) =>
  "/courses/" ++ course.id ++ "/" ++ pageId;

let renderCourse = (pageIdOpt, widgetsState, checkWidget, resetWidget, course) => {
  let pageOpt = pageIdOpt |> Option.flatMap(findPage(course));
  let setCurrentPage = pageId =>
    ReasonReactRouter.push(coursePageUrl(course, pageId));

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
  | None =>
    course.pages
    |> List.hd
    |> Utils.Page.extractId
    |> coursePageUrl(course)
    |> ReasonReactRouter.replace;
    React.null;
  };
};

[@react.component]
let make = (~id: string, ~pageId: option(string)) => {
  let ({courses}, dispatch) = Store.useStore();

  React.useEffect0(() => {
    dispatch(CoursesAction(FetchCourse(id)));
    Some(() => dispatch(CoursesAction(SetCurrentCourse(NotAsked))));
  });

  let checkWidgeet = (id, input) =>
    dispatch(CoursesAction(CheckWidget(id, input)));

  let resetWidget = id =>
    dispatch(CoursesAction(SetWidgetState(id, Initial)));

  Loader.renderResult(
    renderCourse(pageId, courses.widgetsState, checkWidgeet, resetWidget),
    courses.currentCourse,
  );
};