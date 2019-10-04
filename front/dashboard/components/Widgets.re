open Types;
open Store.State;

let renderWidgets = (widgets, onCheck, widgetsState, resetWidget) =>
  widgets
  |> List.map(w => {
       let widgetId = Utils.Widget.extractId(w);
       let state =
         Courses.State.Map.getWithDefault(widgetsState, widgetId, Initial);
       <WidgetComponent
         key={Utils.Widget.extractId(w)}
         widget=w
         state
         onCheck={input => onCheck(widgetId, input)}
         resetWidget={() => resetWidget(widgetId)}
       />;
     })
  |> Array.of_list
  |> React.array;