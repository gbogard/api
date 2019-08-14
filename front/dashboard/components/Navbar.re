type background =
  | Primary
  | Dark;

[@react.component]
let make = (~background: background=Primary, ~onDrawerOpen=?) => {
  let bgClass =
    switch (background) {
    | Primary => "bg-primary text-white"
    | Dark => "bg-dark text-white"
    };

  <>
    <div className="navbar-spacer" />
    <nav className={"navbar navbar-expand-lg navbar-dark " ++ bgClass}>
      {
        switch (onDrawerOpen) {
        | Some(cb) =>
          <i
            onClick=(_ => cb())
            className="hidden-lg menu-icon ion ion-md-menu"
          />
        | None => React.null
        }
      }
      <Link className="navbar-brand" href=Configuration.showcaseUrl>
        {React.string("Lambdacademy")}
      </Link>
    </nav>
  </>;
};
