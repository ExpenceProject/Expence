import { AvatarDialog } from '@/components/custom/avatar-dialog/avatar-dialog';
import { CameraIcon } from '@/components/icons/camera';
import { Avatar } from '@/components/ui/avatar';
import { PageLayout } from '@/layout/page-layout';
import { User, UserUpdatedData } from '@/types';
import { openAvatarModalAtom } from '@/utils/atoms/modal-atoms';
import { useUser } from '@/utils/providers/user-provider/use-user';
import { Box, Flex, Text } from '@chakra-ui/react';
import { AxiosResponse } from 'axios';
import { useAtom } from 'jotai';
import { useMemo } from 'react';
import { HiMiniUser, HiMiniUserGroup } from 'react-icons/hi2';
import { MdMoneyOff } from 'react-icons/md';
import { Outlet } from 'react-router';
import { NavLink } from 'react-router-dom';
import styled from 'styled-components';

export type UserPageOutletContext = {
  user: User;
  updateUser: (updatedUser: UserUpdatedData) => Promise<AxiosResponse>;
  refreshUser: () => Promise<User>;
};

const StyledLink = styled(NavLink)`
  color: var(--ck-colors-text);
  text-decoration: none;
  outline: none;
  padding: 5px;
  border-radius: 5px;
  transition: all 0.15s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  line-height: 30px;
  width: 100%;
  font-size: var(--ck-font-sizes-sm);

  @media (min-width: 480px) {
    font-size: var(--ck-font-sizes-lg);
  }

  @media (min-width: 768px) {
    padding-left: 10px;
    justify-content: flex-start;
  }

  &:hover {
    color: var(--ck-colors-text-hover);
    background-color: var(--ck-colors-hover);
  }

  &.active {
    background-color: var(--ck-colors-primary);
    border: none;
    color: var(--ck-colors-text-bg);
  }
`;

export const ProfilePage = () => {
  const { user, updateUserAvatar, deleteUserAvatar, updateUser, refreshUser } =
    useUser();
  const [, openAvatarModal] = useAtom(openAvatarModalAtom);

  const userAvatarSource = useMemo(
    () =>
      `${import.meta.env.VITE_REACT_IMAGES_URL}/${import.meta.env.VITE_REACT_IMAGES_BUCKET}/${user?.image.key}`,
    [user],
  );

  if (!user) {
    return (
      <PageLayout>
        <Text>Something went wrong...</Text>
      </PageLayout>
    );
  }

  return (
    <PageLayout direction="row">
      <Flex
        w={{ base: '100%', md: '25%' }}
        h="100%"
        justifyContent={{ base: 'center', md: 'unset' }}
      >
        <Flex
          direction="column"
          gap={10}
          w="100%"
          h="min-content"
          p={{ base: 4, md: 7 }}
          borderRadius={10}
          bg="hover"
        >
          <Flex direction="column" alignItems="center">
            <Box position="relative" w={200} h={200}>
              <Avatar
                src={user?.image.key && userAvatarSource}
                variant="solid"
                name={`${user?.firstName} ${user?.lastName}`}
                color="textBg"
                bg="primary"
                nameSize={45}
                contain="content"
                w={200}
                h={200}
                outlineWidth={5}
                outlineColor="primary"
                outlineOffset={-1}
                outlineStyle="solid"
              />
              <Box
                as="button"
                borderRadius="100%"
                padding={2}
                cursor="pointer"
                _hover={{
                  bg: 'hover',
                }}
                position="absolute"
                bottom={-1}
                right={3}
                bg="background"
                outlineWidth={2}
                outlineColor="primary"
                outlineOffset={-1}
                outlineStyle="solid"
                transition="all 0.15s ease"
                onClick={openAvatarModal}
              >
                <CameraIcon />
              </Box>
            </Box>
          </Flex>
          <Text
            fontSize="2xl"
            fontWeight="bold"
            color="textRaw"
            pl={{ base: 0, md: '10px' }}
            textAlign={{ base: 'center', md: 'unset' }}
          >
            {user?.firstName} {user?.lastName}
          </Text>
          <Flex direction={{ base: 'row', md: 'column' }} gap={3} w="100%">
            <StyledLink to="/profile" end>
              <HiMiniUser size={20} /> Profile
            </StyledLink>
            <StyledLink to="groups">
              <HiMiniUserGroup size={20} /> Groups
            </StyledLink>
            <StyledLink to="debt">
              <MdMoneyOff size={20} /> Debt
            </StyledLink>
          </Flex>
        </Flex>
      </Flex>
      <Flex
        w={{ base: '100%', md: '75%' }}
        h="100%"
        justifyContent={{ base: 'center', md: 'unset' }}
        pb={10}
      >
        <Outlet context={{ user, updateUser, refreshUser }} />
      </Flex>
      <AvatarDialog
        user={user ?? undefined}
        updateUserAvatar={updateUserAvatar}
        deleteUserAvatar={deleteUserAvatar}
        refreshUser={refreshUser}
      />
    </PageLayout>
  );
};
