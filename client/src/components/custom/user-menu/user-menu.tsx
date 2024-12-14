import { Avatar } from '@/components/ui/avatar';
import {
  MenuContent,
  MenuItem,
  MenuRoot,
  MenuTrigger,
} from '@/components/ui/menu';
import { useUser } from '@/utils/providers/user-provider/use-user';
import { useRef } from 'react';
import { HiMiniArrowRight } from 'react-icons/hi2';
import { useNavigate } from 'react-router';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

const StyledUserMenuLink = styled(Link)`
  color: var(--ck-colors-text-raw);
  text-decoration: none;
  width: 100%;
  padding: 8px;
  outline: none;
`;

const UserMenu = () => {
  const { user, logout } = useUser();
  const avatarRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  if (!user) {
    return null;
  }

  const getAnchorRect = () => {
    const avatar = avatarRef.current;

    if (!avatar) {
      return null;
    }

    return avatar.getBoundingClientRect();
  };

  const handleLogout = () => {
    navigate('/');
    logout();
  };

  return (
    <MenuRoot positioning={{ getAnchorRect }}>
      <MenuTrigger asChild>
        <Avatar
          ref={avatarRef}
          variant="solid"
          name={`${user?.firstName} ${user?.lastName}`}
          color="textBg"
          bg="primary"
          cursor="pointer"
        />
      </MenuTrigger>
      <MenuContent p={2}>
        <MenuItem
          value="profile"
          cursor="pointer"
          p={0}
          _hover={{ bgColor: 'hover' }}
          transition="all 0.15s ease"
          borderRadius={5}
        >
          <StyledUserMenuLink to="/profile">Profile</StyledUserMenuLink>
        </MenuItem>
        <MenuItem
          value="my-groups"
          cursor="pointer"
          p={0}
          _hover={{ bgColor: 'hover' }}
          borderRadius={5}
          transition="all 0.15s ease"
        >
          <StyledUserMenuLink to="/">My groups</StyledUserMenuLink>
        </MenuItem>
        <MenuItem
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
        >
          Logout
          <HiMiniArrowRight />
        </MenuItem>
      </MenuContent>
    </MenuRoot>
  );
};

export default UserMenu;
