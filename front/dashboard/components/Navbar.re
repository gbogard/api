type background =
  | Primary
  | Dark;

[@react.component]
let make = (~background: background=Primary) => {
  let bgClass =
    switch (background) {
    | Primary => "bg-primary text-white"
    | Dark => "bg-dark text-white"
    };

  <nav className={"navbar navbar-expand-lg navbar-dark " ++ bgClass}>
    <Link className="navbar-brand" href=Configuration.showcaseUrl>
      {React.string("Lambdacademy")}
    </Link>
  </nav>;
};
