open Types;
open ReactUpdate;
open Js.Promise;
open Belt;

type action =
  | SetCourses(result(list(courseManifest), nothing))
  | SetCurrentCourse(result(course, nothing))
  | SetCurrentPageId(option(string))
  | FetchCourses
  | FetchCourse(string);

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
         send(SetCurrentPageId(pageId));
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
    currentPageId: option(string),
    widgetsState: Map.t(widgetState),
  };

  let initial = {
    list: NotAsked,
    currentCourse: NotAsked,
    currentPageId: None,
    widgetsState: Map.empty,
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
    | SetCurrentPageId(id) => Update({...state, currentPageId: id})
    | SetCourses(courses) => Update({...state, list: courses})
    | SetCurrentCourse(course) => Update({...state, currentCourse: course})
    };
};
