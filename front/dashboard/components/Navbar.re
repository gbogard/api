open Rationale;
open Types;
open Links;

type background =
  | Primary
  | Dark;

[@react.component]
let make = (~background: background=Primary, ~onDrawerOpen=?, ~course=?) => {
  let bgClass =
    switch (background) {
    | Primary => "bg-primary text-white"
    | Dark => "bg-dark text-white"
    };

  let brand =
    <Link className="navbar-brand" href=dashboardHomeUrl>
      {React.string("Lambdacademy")}
    </Link>;

  let burgerButton =
    onDrawerOpen
    |> Option.map(cb =>
         <i
           onClick={_ => cb()}
           className="hidden-lg menu-icon ion ion-md-menu"
         />
       )
    |> Option.default(React.null);

  let courseTitle =
    course
    |> Option.map(course =>
         <div
           className="course-title"
           onClick={_ => ReasonReactRouter.push(coursesUrl)}>
           <i className="ion ion-md-arrow-round-back" />
           {React.string(course.title)}
         </div>
       )
    |> Option.default(React.null);

  <>
    <div className="navbar-spacer" />
    <nav className={"navbar navbar-dark " ++ bgClass}>
      burgerButton
      brand
      courseTitle
    </nav>
  </>;
};
