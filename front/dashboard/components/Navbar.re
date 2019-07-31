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
    <a className="navbar-brand" href="#"> {React.string("Lambdacademy")} </a>
    <ul className="navbar-nav mr-auto">
      <li className="nav-item active">
        <a className="nav-link" href="#"> {React.string("Lambdacademy")} </a>
      </li>
    </ul>
  </nav>;
};
