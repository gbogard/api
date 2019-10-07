module App = {
  [@react.component]
  let make = () => {
    <Store.Provider>
      <> <BsReactHelmet defaultTitle="Lambdacademy Dashboard" /> <Router /> </>
    </Store.Provider>;
  };
};

ReactDOMRe.renderToElementWithId(<App />, "app");