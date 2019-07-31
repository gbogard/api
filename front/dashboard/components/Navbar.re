type background =
  | Primary
  | Dark;

[@react.component]
let make = (~background: background=Primary) => {
  let bgClass =
    switch (background) {
    | Primary => "bg-primary"
    | Dark => "bg-dark"
    };

  <nav className={"navbar navbar-expand-lg navbar-dark " ++ bgClass}>
    <Link className="navbar-brand" href=Configuration.showcaseUrl>
      {React.string("Lambdacademy")}
    </Link>
    <ul className="navbar-nav mr-auto">
      <li className="nav-item active">
        <Link className="nav-link" href="/">
          {React.string("Lambdacademy")}
        </Link>
      </li>
    </ul>
  </nav>;
};
