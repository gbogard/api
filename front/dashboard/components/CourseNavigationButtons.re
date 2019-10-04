open Types;
open Rationale;

[@react.component]
let make = (~course, ~currentPageId, ~setCurrentPage) => {
  let currentPageIndex =
    course.pages
    |> RList.findIndex(p => Utils.Page.extractId(p) === currentPageId);
  let previousPage =
    currentPageIndex
    |> Option.map(i => i - 1)
    |> Option.flatMap(RList.nth(_, course.pages));
  let nextPage =
    currentPageIndex
    |> Option.map(i => i + 1)
    |> Option.flatMap(RList.nth(_, course.pages));
  let navigate = (page, _) => setCurrentPage(Utils.Page.extractId(page));

  <div className="spaced-container with-padding-y">
    <div>
      {
        previousPage
        |> Option.map(page =>
             <Button onClick={navigate(page)}>
               <i className="ion ion-ios-arrow-back" />
               {React.string(Utils.Page.extractTitle(page))}
             </Button>
           )
        |> Option.default(React.null)
      }
    </div>
    <div>
      {
        nextPage
        |> Option.map(page =>
             <Button onClick={navigate(page)}>
               {React.string(Utils.Page.extractTitle(page))}
               <i className="ion ion-ios-arrow-forward" />
             </Button>
           )
        |> Option.default(React.null)
      }
    </div>
  </div>;
}