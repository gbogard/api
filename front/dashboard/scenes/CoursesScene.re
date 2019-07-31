open Store.State;
open Types;
open Courses;

module CoursesList = {
  let make = courses =>
    <ul>
      {
        courses
        |> List.map(course => <li> {React.string(course.title)} </li>)
        |> Array.of_list
        |> React.array
      }
    </ul>;
};

[@react.component]
let make = () => {
  let ({courses}, dispatch) = Store.useStore();

  React.useEffect0(() => {
    dispatch(CoursesAction(FetchCourses));
    None;
  });

  <div>
    <Navbar />
    <h1> {React.string("Courses list")} </h1>
    <ul> {Loader.renderResult(CoursesList.make, courses.list)} </ul>
  </div>;
};
