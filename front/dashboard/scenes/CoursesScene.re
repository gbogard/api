open Store.State;
open Types;
open Courses;

module CoursesList = {
  let make = courseManifests =>
    <div className="grid">
      {
        courseManifests
        |> List.map((course: courseManifest) =>
             <div className="col col-3 col-md" key={course.id}>
               <CourseItem course />
             </div>
           )
        |> Array.of_list
        |> React.array
      }
    </div>;
};

[@react.component]
let make = () => {
  let ({courses}, dispatch) = Store.useStore();

  React.useEffect0(() => {
    dispatch(CoursesAction(FetchCourses));
    None;
  });

  <>
    <Navbar background=Navbar.Dark />
    <div className="container">
      <h3> {React.string("Courses list")} </h3>
      <br />
      {Loader.renderResult(CoursesList.make, courses.list)}
    </div>
  </>;
};
