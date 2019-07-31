open Types;
open ReactUpdate;
open Js.Promise;

type action =
  | SetCourses(result(list(courseManifest)))
  | SetCurrentCourse(result(course))
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
    |> then_(res => resolve(send(SetCurrentCourse(res))))
    |> ignore;
    None;
  };
};

module State = {
  type t = {
    list: result(list(courseManifest)),
    currentCourse: result(course),
  };

  let initial = {list: NotAsked, currentCourse: NotAsked};

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
    };
};
