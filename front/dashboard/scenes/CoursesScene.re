open Utils;
open Js.Promise;

[@react.component]
let make = () => {
  let (courses, setCourses) = React.useState(() => NotAsked);
  let (store, _) = Store.useStore();

  Js.Console.log(store)

  let fetchData = () =>
    CoursesService.fetchCourses()
    |> then_(res => {
         setCourses(_ => res);
         resolve();
       })
    |> ignore;

  React.useEffect0(() => {
    fetchData();
    None;
  });

  <div> {React.string("Courses List")} </div>;
};