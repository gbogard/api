open Types;
open Store.State;
open Courses;

[@react.component]
let make = (~id: string) => {
  let ({courses}, dispatch) = Store.useStore();
  React.useEffect0(() => {
    dispatch(CoursesAction(FetchCourse(id)));
    None;
  });

  <div>
    <Navbar />
    <h1> {React.string("Course details")} </h1>
    {
      Loader.renderResult(
        course => <h2> {React.string(course.title)} </h2>,
        courses.currentCourse,
      )
    }
  </div>;
};
