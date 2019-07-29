open HttpClient;
open Serialization;

let fetchCourses = () => fetchJson(
  ~endpoint="/courses",
  ~decoder=Decode.courseManifests
)()