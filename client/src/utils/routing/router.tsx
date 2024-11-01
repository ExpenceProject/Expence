import { createBrowserRouter } from 'react-router-dom';

import App from '../../App';
import { LandingPage } from '../../pages/landing/landing';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    // TODO: errorElement: (),
    children: [
      {
        index: true,
        element: <LandingPage />,
      },
    ],
  },
]);
