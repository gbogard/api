open Types;
open ReactUpdate;
open Js.Promise;
open Belt;

type action =
  | SetCourses(result(list(courseManifest), nothing))
  | SetCurrentCourse(result(course, nothing))
  | FetchCourses
  | FetchCourse(string)
  | CheckWidget(string, WidgetInput.t)
  | SetWidgetState(string, widgetState);

module Effects = {
  let fetchCourses = ({send}) => {
    CoursesService.fetchCourses()
    |> then_(res => resolve(send(SetCourses(res))))
    |> ignore;
    None;
  };

  let fetchCourse = (id, {send}) => {
    CoursesService.fetchCourse(id)
    |> then_(res => {
         let pageId =
           res
           ->Utils.Result.toOption
           ->Option.map(course => course.pages)
           ->Option.flatMap(List.head)
           ->Option.map(Utils.Page.extractId);
         send(SetCurrentCourse(res));
         resolve();
       })
    |> ignore;
    None;
  };

  let checkWidget = (id, widgetInput, {send}) => {
    CoursesService.checkWidget(id, widgetInput)
    |> then_(widgetState => {
         send(SetWidgetState(id, widgetState));
         resolve();
       })
    |> ignore;
    None;
  };
};

module State = {
  module Map = Belt.Map.String;

  type t = {
    list: result(list(courseManifest), nothing),
    currentCourse: result(course, nothing),
    widgetsState: Map.t(widgetState),
  };

  let initial = {
    list: NotAsked,
    currentCourse: NotAsked,
    widgetsState: Map.empty,
  };

  let setWidgetState = (widgetId, widgetState, state) => {
    ...state,
    widgetsState: Map.set(state.widgetsState, widgetId, widgetState),
  };

  let reducer = (action, state) =>
    switch (action) {
    | FetchCourses =>
      UpdateWithSideEffects({...state, list: Loading}, Effects.fetchCourses)
    | FetchCourse(id) =>
      UpdateWithSideEffects(
        {...state, currentCourse: Loading},
        Effects.fetchCourse(id),
      )
    | SetCourses(courses) => Update({...state, list: courses})
    | SetCurrentCourse(course) => Update({...state, currentCourse: course})
    | SetWidgetState(id, widgetState) =>
      Update(setWidgetState(id, widgetState, state))
    | CheckWidget(id, input) =>
      UpdateWithSideEffects(
        setWidgetState(id, Pending, state),
        Effects.checkWidget(id, input),
      )
    };
};
