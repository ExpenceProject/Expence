import { Avatar } from '@/components/ui/avatar';
import { useColorModeValue } from '@/components/ui/color-mode';
import {
  DialogBackdrop,
  DialogBody,
  DialogCloseTrigger,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogRoot,
  DialogTitle,
} from '@/components/ui/dialog';
import { User } from '@/types';
import {
  closeAvatarModalAtom,
  isAvatarModalOpenAtom,
} from '@/utils/atoms/modal-atoms';
import { Button, Flex, Input, Text } from '@chakra-ui/react';
import { AxiosResponse } from 'axios';
import { useAtom } from 'jotai';
import { FC, useMemo, useRef, useState } from 'react';
import { FiUpload } from 'react-icons/fi';
import { IoMdCheckmark } from 'react-icons/io';
import { MdDelete } from 'react-icons/md';
import { toast } from 'react-toastify';

type AvatarDialogProps = {
  user?: User;
  updateUserAvatar: (image: FormData) => Promise<AxiosResponse>;
  deleteUserAvatar: () => Promise<AxiosResponse>;
  refreshUser: () => Promise<User>;
};

export const AvatarDialog: FC<AvatarDialogProps> = ({
  user,
  updateUserAvatar,
  deleteUserAvatar,
  refreshUser,
}) => {
  const currentTheme = useColorModeValue('light', 'dark');
  const [isAvatarModalOpen] = useAtom(isAvatarModalOpenAtom);
  const [uploadedImage, setUploadedImage] = useState<File | null>(null);
  const [, closeAvatarModal] = useAtom(closeAvatarModalAtom);
  const inputRef = useRef<HTMLInputElement>(null);

  const handleAvatarUpdate = () => {
    if (uploadedImage) {
      const formData = new FormData();
      formData.append('image', uploadedImage);

      updateUserAvatar(formData)
        .then(async () => {
          await refreshUser();
          toast.success('Avatar updated successfully');
        })
        .catch((error) => {
          console.error(error);
          toast.error(
            'Failed to update avatar, please try again in a few minutes',
          );
        })
        .finally(() => {
          closeAvatarModal();
          setUploadedImage(null);
        });
    } else {
      closeAvatarModal();
    }
  };

  const handleAvatarDelete = () => {
    deleteUserAvatar()
      .then(async () => {
        await refreshUser();
        toast.success('Avatar deleted successfully');
      })
      .catch((error) => {
        console.error(error);
        toast.error(
          'Failed to delete avatar, please try again in a few minutes',
        );
      })
      .finally(() => {
        closeAvatarModal();
        setUploadedImage(null);
      });
  };

  const handleInputClick = () => {
    if (inputRef.current) {
      inputRef.current.click();
    }
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const files = event.target.files;
    if (files && files.length > 0) {
      const file = files[0];
      setUploadedImage(file);
      console.log('File:', file);
    }
  };

  const userAvatarSource = useMemo(
    () =>
      uploadedImage
        ? URL.createObjectURL(uploadedImage)
        : user?.image.key &&
          `${import.meta.env.VITE_REACT_IMAGES_URL}/${import.meta.env.VITE_REACT_IMAGES_BUCKET}/${user.image.key}`,
    [user, uploadedImage],
  );

  return (
    <DialogRoot
      placement="center"
      size="lg"
      scrollBehavior="inside"
      open={isAvatarModalOpen}
      onInteractOutside={closeAvatarModal}
      onEscapeKeyDown={closeAvatarModal}
      motionPreset="slide-in-bottom"
    >
      <DialogBackdrop
        bg="rgba(0, 0, 0, 0.4)"
        _dark={{ bg: 'rgba(0, 0, 0, 0.7 )' }}
        css={{ backdropFilter: 'blur(2px)' }}
      />
      <DialogContent shadow="xl" p="4">
        <DialogCloseTrigger _icon={{ w: 6, h: 6 }} onClick={closeAvatarModal} />
        <DialogHeader>
          <DialogTitle fontSize="xl">Change your photo</DialogTitle>
        </DialogHeader>
        <DialogBody
          data-theme={currentTheme}
          display="flex"
          alignItems="center"
          justifyContent="center"
          flexDirection="column"
          p={{ base: 2, md: 8 }}
        >
          <Avatar
            src={userAvatarSource}
            variant="solid"
            name={`${user?.firstName} ${user?.lastName}`}
            color="textBg"
            bg="primary"
            nameSize={45}
            contain="content"
            w="200px"
            h="200px"
            className="modalAvatar"
            outlineWidth={4}
            outlineColor="primary"
            outlineOffset={-1}
            outlineStyle="solid"
          />
          <Flex
            w="100%"
            justifyContent="center"
            alignItems="center"
            direction={'column'}
            pt={10}
            gap={10}
          >
            {uploadedImage && (
              <Text truncate maxW="80%">
                Source: {uploadedImage.name}
              </Text>
            )}
            <Flex
              w="100%"
              justifyContent="center"
              alignItems="center"
              gap={{ base: 4, md: 8 }}
            >
              <Button
                display="flex"
                alignItems="center"
                justifyContent="center"
                flex={1}
                color="textBg"
                _hover={{ bg: 'hoverPrimary' }}
                bgColor="primary"
                fontSize={{ base: 'sm', md: 'md' }}
                onClick={handleAvatarUpdate}
              >
                <IoMdCheckmark /> Save
              </Button>
              <Button
                display="flex"
                alignItems="center"
                justifyContent="center"
                flex={1}
                color="textBg"
                _hover={{ bg: 'hoverPrimary' }}
                bgColor="primary"
                fontSize={{ base: 'md', md: 'md' }}
                onClick={handleInputClick}
              >
                <FiUpload /> Upload
              </Button>
              <Input
                type="file"
                ref={inputRef}
                display="none"
                onChange={handleFileChange}
                accept="image/*"
                max={1}
              />
              <Button
                display="flex"
                alignItems="center"
                justifyContent="center"
                flex={1}
                variant="outline"
                outlineColor="textError"
                _hover={{ bg: 'backgroundErrorHover' }}
                color="textError"
                bgColor="backgroundError"
                fontSize={{ base: 'sm', md: 'md' }}
                onClick={handleAvatarDelete}
              >
                <MdDelete />
                Delete
              </Button>
            </Flex>
          </Flex>
        </DialogBody>
        <DialogFooter fontSize="md"></DialogFooter>
      </DialogContent>
    </DialogRoot>
  );
};
