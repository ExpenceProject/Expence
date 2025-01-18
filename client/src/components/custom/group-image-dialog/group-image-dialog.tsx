import GroupIcon from '@/assets/images/group_icon.svg';
import { Avatar } from '@/components/ui/avatar';
import { useColorModeValue } from '@/components/ui/color-mode';
import {
  DialogBackdrop,
  DialogBody,
  DialogCloseTrigger,
  DialogContent,
  DialogHeader,
  DialogRoot,
  DialogTitle,
} from '@/components/ui/dialog';
import { GroupWithMembers } from '@/types';
import {
  closeGroupEditImageModalAtom,
  isGroupEditImageModalOpenAtom,
} from '@/utils/atoms/modal-atoms';
import { apiClient } from '@/utils/http-clients/api-http-client';
import { Button, Flex, Input, Text } from '@chakra-ui/react';
import { useAtom } from 'jotai';
import { FC, useMemo, useRef, useState } from 'react';
import { FiUpload } from 'react-icons/fi';
import { IoMdCheckmark } from 'react-icons/io';
import { toast } from 'react-toastify';

type GroupEditImageModalProps = {
  group: GroupWithMembers;
  getGroupAndMembers: () => void;
};

export const GroupEditImageModal: FC<GroupEditImageModalProps> = ({
  group,
  getGroupAndMembers,
}) => {
  const currentTheme = useColorModeValue('light', 'dark');
  const [isGroupEditImageModalOpen] = useAtom(isGroupEditImageModalOpenAtom);
  const [uploadedImage, setUploadedImage] = useState<File | null>(null);
  const [, closeGroupEditImageModal] = useAtom(closeGroupEditImageModalAtom);
  const inputRef = useRef<HTMLInputElement>(null);

  const handleGroupImageUpdate = () => {
    if (uploadedImage) {
      const formData = new FormData();
      formData.append('name', group.name);
      formData.append('file', uploadedImage);

      apiClient
        .put(`/groups/${group.id}`, formData)
        .then(() => {
          getGroupAndMembers();
          toast.success('Group image updated successfully');
        })
        .catch((error) => {
          console.error(error);
          toast.error(
            'Failed to update group image, please try again in a few minutes',
          );
        })
        .finally(() => {
          closeGroupEditImageModal();
          setUploadedImage(null);
        });
    } else {
      closeGroupEditImageModal();
    }
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
    }
  };

  const groupImageSource = useMemo(
    () =>
      uploadedImage
        ? URL.createObjectURL(uploadedImage)
        : group?.image.key &&
          `${import.meta.env.VITE_REACT_IMAGES_URL}/${import.meta.env.VITE_REACT_IMAGES_BUCKET}/${group.image.key}`,
    [group, uploadedImage],
  );

  return (
    <DialogRoot
      placement="center"
      size="lg"
      scrollBehavior="inside"
      open={isGroupEditImageModalOpen}
      onInteractOutside={closeGroupEditImageModal}
      onEscapeKeyDown={closeGroupEditImageModal}
      motionPreset="slide-in-bottom"
    >
      <DialogBackdrop
        bg="rgba(0, 0, 0, 0.4)"
        _dark={{ bg: 'rgba(0, 0, 0, 0.7 )' }}
        css={{ backdropFilter: 'blur(2px)' }}
      />
      <DialogContent
        shadow="xl"
        p="4"
        css={{
          position: 'fixed',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
        }}
      >
        <DialogCloseTrigger
          _icon={{ w: 6, h: 6 }}
          onClick={closeGroupEditImageModal}
        />
        <DialogHeader>
          <DialogTitle fontSize="xl">Change Group Image</DialogTitle>
        </DialogHeader>
        <DialogBody
          data-theme={currentTheme}
          display="flex"
          alignItems="center"
          justifyContent="center"
          flexDirection="column"
          p={6}
        >
          <Avatar
            src={groupImageSource || GroupIcon}
            variant="solid"
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
                onClick={handleGroupImageUpdate}
              >
                <IoMdCheckmark />
                <Text display={{ base: 'none', sm: 'unset' }}>Save</Text>
              </Button>
              <Button
                display="flex"
                alignItems="center"
                justifyContent="center"
                flex={1}
                color="textBg"
                _hover={{ bg: 'hoverPrimary' }}
                bgColor="primary"
                fontSize={{ base: 'sm', md: 'md' }}
                onClick={handleInputClick}
              >
                <FiUpload />{' '}
                <Text display={{ base: 'none', sm: 'unset' }}>Upload</Text>
              </Button>
              <Input
                type="file"
                ref={inputRef}
                display="none"
                onChange={handleFileChange}
                accept="image/*"
                max={1}
              />
            </Flex>
          </Flex>
        </DialogBody>
      </DialogContent>
    </DialogRoot>
  );
};
