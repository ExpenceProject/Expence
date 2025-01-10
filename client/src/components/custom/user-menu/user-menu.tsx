import { Avatar } from '@/components/ui/avatar';
import {
  MenuContent,
  MenuItem,
  MenuRoot,
  MenuTrigger,
} from '@/components/ui/menu';
import { useUser } from '@/utils/providers/user-provider/use-user';
import { Flex } from '@chakra-ui/react';
import { useMemo, useRef, useState } from 'react';
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

export const UserMenu = () => {
  const { user, logout } = useUser();
  const avatarRef = useRef<HTMLDivElement>(null);
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const navigate = useNavigate();

  const userImage = useMemo(() => {
    if (!user?.image.key) {
      return;
    }

    return `${import.meta.env.VITE_REACT_IMAGES_URL}/${import.meta.env.VITE_REACT_IMAGES_BUCKET}/${user.image.key}`;
  }, [user]);

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
    <MenuRoot
      positioning={{ getAnchorRect }}
      open={isMenuOpen}
      onOpenChange={(e) => setIsMenuOpen(e.open)}
      onInteractOutside={() => setIsMenuOpen(false)}
    >
      <MenuTrigger asChild>
        <Avatar
          ref={avatarRef}
          variant="solid"
          src={userImage}
          name={`${user?.firstName} ${user?.lastName}`}
          color="textBg"
          bg="primary"
          outlineWidth={2}
          outlineColor="primary"
          outlineOffset={-1}
          outlineStyle="solid"
          cursor="pointer"
        />
      </MenuTrigger>
      <MenuContent p={2.5}>
        <Flex direction="column" gap={2}>
          <MenuItem
            value="profile"
            cursor="pointer"
            p={0}
            _hover={{ bgColor: 'hover' }}
            transition="all 0.15s ease"
            borderRadius={5}
            bg={'background'}
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
            bg={'background'}
          >
            <StyledUserMenuLink to="/profile/groups">
              My groups
            </StyledUserMenuLink>
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
            bg={'backgroundError'}
          >
            Logout
            <HiMiniArrowRight />
          </MenuItem>
        </Flex>
      </MenuContent>
    </MenuRoot>
  );
};
