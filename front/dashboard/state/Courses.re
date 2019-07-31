open Types;
open ReactUpdate;
open Js.Promise;

type action =
  | SetCourses(result(list(courseManifest)))
  | FetchCourses;

module Effects = {

  let fetchCourses = ({ send }) => {
    CoursesService.fetchCourses()
      |> then_(res => resolve(send(SetCourses(res))))
      |> ignore
    None
  }
};

module State = {
  type t = {
    courses: result(list(courseManifest))
  }

  let initial = {
    courses: NotAsked
  }

  let reducer = (action, _state) =>
    switch (action) {
    | FetchCourses => UpdateWithSideEffects({courses: Loading}, Effects.fetchCourses) 
    | SetCourses(courses) => Update({courses: courses})
    };
};