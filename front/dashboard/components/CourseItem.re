open Types;
open Css;

let defaultDescription = "This course has no description.";

let courseImg =
  style([
    backgroundImage(
      url(
        "https://scala-lang.org/resources/img/frontpage/background-header-home.jpg",
      ),
    ),
  ]);

[@react.component]
let make = (~course: courseManifest) =>
  <Link href={"/courses/" ++ course.id}>
    <div className="course-item">
      <div className={"img-container " ++ courseImg} />
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
