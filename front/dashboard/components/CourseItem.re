open Types;
open Css;

let defaultDescription = "This course has no description.";
let defaultImage = "https://scala-lang.org/resources/img/frontpage/background-header-home.jpg";

let courseImg = courseManifest =>
  style([
    backgroundImage(
      url(Belt.Option.getWithDefault(courseManifest.image, defaultImage)),
    ),
  ]);

[@react.component]
let make = (~course: courseManifest) =>
  <Link href={"/courses/" ++ course.id}>
    <div className="course-item">
      <div className={"img-container " ++ courseImg(course)} />
      <h5 className="title"> {React.string(course.title)} </h5>
      <div className="description py-2 px-3">
        <p>
          {
            React.string(
              if (String.length(course.description) > 0) {
                course.description;
              } else {
                defaultDescription;
              },
            )
          }
        </p>
      </div>
    </div>
  </Link>;
