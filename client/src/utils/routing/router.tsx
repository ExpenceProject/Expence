import App from '@/App';
import { UserGroups } from '@/components/custom/user-groups/user-groups';
import { UserInformation } from '@/components/custom/user-information/user-information';
import { GroupPage } from '@/pages/group/group';
import { LandingPage } from '@/pages/landing/landing';
import { PageNotFound } from '@/pages/page-not-found/page-not-found';
import { ProfilePage } from '@/pages/profile/profile';
import { createBrowserRouter } from 'react-router-dom';

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
      {
        path: '/profile',
        element: <ProfilePage />,
        children: [
          {
            path: '',
            element: <UserInformation />,
          },
          {
            path: 'groups',
            element: <UserGroups />,
          },
        ],
      },
      {
        path: '/groups/:groupId',
        element: <GroupPage />,
      },
    ],
  },
  {
    path: '*',
    element: <PageNotFound />,
  },
]);
