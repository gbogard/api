open HttpClient;
open Serialization;

let fetchCourses = () => fetchJson(
  ~endpoint="/courses",
  ~decoder=Decode.courseManifests
)()

let courses: Js.Promise.t(result(list(Types.courseManifest))) = fetchCourses()