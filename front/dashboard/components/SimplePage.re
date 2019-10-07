open Rationale;
open Types;

[@react.component]
let make =
    (
      ~page: Page.simplePage,
      ~course,
      ~setCurrentPage,
      ~widgetsState,
      ~checkWidget,
      ~resetWidget,
    ) => {
  let (isDrawerOpen, setDrawerOpen) = React.useState(() => false);
  let contentRef = React.useRef(Js.Nullable.null);
  let url = ReasonReactRouter.useUrl();
  React.useEffect1(
    () => {
      contentRef
      |> React.Ref.current
      |> Js.Nullable.toOption
      |> Belt.Option.getExn
      |> Interop.Dom.scrollTo(_, 0, 0);
      None;
    },
    [|url|],
  );

  let firstPageId =
    course.pages |> RList.head |> Option.map(Utils.Page.extractId);
  let isFirstPage =
    firstPageId |> Option.map(id => id === page.id) |> Option.default(false);
  let navigation =
    <PageNavigation
      pages={course.pages}
      currentPageId={page.id}
      onChange=setCurrentPage
    />;

  <>
    <BsReactHelmet>
      <title>
        {React.string(
           page.title ++ " - " ++ course.title ++ " - Lambdacademy",
         )}
      </title>
    </BsReactHelmet>
    <Interop.Drawer
      _open=isDrawerOpen onChange={state => setDrawerOpen(_ => state)}>
      navigation
    </Interop.Drawer>
    <div className="simple-page-layout">
      <Navbar onDrawerOpen={() => setDrawerOpen(_ => true)} />
      navigation
      <div
        className="content container-fluid"
        ref={ReactDOMRe.Ref.domRef(contentRef)}>
        {if (isFirstPage) {
           <Box title="About this course">
             {React.string(course.description)}
           </Box>;
         } else {
           React.null;
         }}
        <div>
          <h2> {React.string(page.title)} </h2>
          {Widgets.renderWidgets(
             page.widgets,
             checkWidget,
             widgetsState,
             resetWidget,
           )}
          <CourseNavigationButtons
            course
            currentPageId={page.id}
            setCurrentPage
          />
        </div>
      </div>
    </div>
  </>;
};