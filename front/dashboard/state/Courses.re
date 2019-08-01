open Types;
open ReactUpdate;
open Js.Promise;
open Belt;

type action =
  | SetCourses(result(list(courseManifest)))
  | SetCurrentCourse(result(course))
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
  type t = {
    list: result(list(courseManifest)),
    currentCourse: result(course),
    currentPageId: option(string),
  };

  let initial = {
    list: NotAsked,
    currentCourse: NotAsked,
    currentPageId: None,
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
