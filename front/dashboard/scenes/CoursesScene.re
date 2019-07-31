open Types;
open Store;
open Courses;

open Js.Promise;

[@react.component]
let make = () => {
  let (store, dispatch) = Store.useStore();

  React.useEffect0(() => {
    dispatch(CoursesAction(FetchCourses))
    None
  });

  <div> {React.string("Courses List")} </div>;
};