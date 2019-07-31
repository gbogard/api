open HttpClient;
open Serialization;

let fetchCourses =
  fetchJson(~endpoint="/courses", ~decoder=Decode.courseManifests);

let fetchCourse = id =>
  fetchJson(~endpoint="/courses/" ++ id, ~decoder=Decode.course, ());
