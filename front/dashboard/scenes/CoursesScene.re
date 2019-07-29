open Utils;

[@react.component]
let make = () => {
  let (courses, setCourses) = React.useState(() => NotAsked);
  <div>
    {React.string("Courses List")}
  </div>
}