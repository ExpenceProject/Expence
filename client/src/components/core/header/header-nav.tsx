import { useUser } from '@/utils/providers/user-provider/use-user';
import { Button, Flex, useBreakpointValue } from '@chakra-ui/react';
import { FC } from 'react';
import { useNavigate } from 'react-router-dom';

import HeaderLink from './header-link';

type HeaderNavProps = {
  direction?: 'row' | 'column';
  closeDrawer?: () => void;
};

const HeaderNav: FC<HeaderNavProps> = ({ direction = 'row', closeDrawer }) => {
  const { user, logout } = useUser();
  const navigate = useNavigate();
  const handleLogout = () => {
    navigate('/');
    logout();
    if (closeDrawer) {
      closeDrawer();
    }
  };

  const currentBreakpoint = useBreakpointValue({
    sm: 'sm',
    md: 'md',
    lg: 'lg',
    xl: 'xl',
  });

  return (
    <Flex flexDirection={direction} align="center" justify="center" gap="3">
      {!currentBreakpoint && user && (
        <>
          {' '}
          <HeaderLink text="Profile" href="/profile" onClick={closeDrawer} />
          <HeaderLink
            text="My groups"
            href="/profile/groups"
            onClick={closeDrawer}
          />
        </>
      )}
      <HeaderLink text="About" href="/" onClick={closeDrawer} />
      {!currentBreakpoint && user && (
        <Button
          value="logout"
          color="textError"
          display="flex"
          gap={1}
          cursor="pointer"
          onClick={handleLogout}
          _hover={{
            bgColor: 'backgroundErrorHover',
          }}
          p={2}
          transition="all 0.15s ease"
          borderRadius={5}
          bg={'backgroundError'}
          fontSize="lg"
          w="100%"
        >
          Logout
        </Button>
      )}
    </Flex>
  );
};

export default HeaderNav;
